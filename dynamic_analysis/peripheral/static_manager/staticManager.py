import sys
import os
from subprocess import Popen, PIPE, TimeoutExpired, run

def runStaticAnalysis(inputPath):
    A11Y_PATH = os.environ['A11Y_PATH']
    JAR_PATH = os.path.join(A11Y_PATH, 'code', 'static_analysis', 'out', 'artifacts', 'static_analysis_jar', 'static_analysis.jar')
    outputPath  = os.path.join(A11Y_PATH, 'reports', 'staticReports')
    timeoutReportPath = os.path.join(A11Y_PATH, 'reports', 'staticTimeoutSamples.txt')

    # Get the list of apks already have reports
    alreadyReported = []
    for root, dirs, files in os.walk(outputPath):
        for file in files:
            if file.endswith(".json"):
                alreadyReported.append(file.split('.')[0])
    print("Numbers already reported: " + str(len(alreadyReported)))

    # Remove those already have reports
    apksToProcess = []
    for (path, subdirs, files) in os.walk(inputPath):
        for file in files:
            if not file.endswith('.apk'):
                continue
            if file.split('.')[0] in alreadyReported:
                continue
            apksToProcess.append(os.path.join(path, file))

    print("Need to process: " + str(len(apksToProcess)) + " apks")

    # Run static analysis
    cmdsList = [['java', '-jar', JAR_PATH, apkName, outputPath] for apkName in apksToProcess]

    # Dispatch the commands
    # No more than 10 processses in parallel
    # set a timeout of 300 seconds per process

    timeoutSamples = []

    procs = []
    while True:
        while cmdsList and len(procs) < 10:
            cmd = cmdsList.pop(0)
            print("Running " + cmd[3])
            proc = Popen(cmd, stdout=PIPE, stderr=PIPE)
            procs.append(proc)

        for proc in procs:
            proc.poll()
            if proc.returncode is not None:
                procs.remove(proc)
                continue
            try:
                outs, errs = proc.communicate(timeout=120)
            except TimeoutExpired:
                proc.kill()
                outs, errs = proc.communicate()
                procs.remove(proc)
                timeoutSamples.append(cmd[3])
        if not cmdsList and not procs:
            break


    # output the timeout samples
    with open(timeoutReportPath, 'w') as f:
        for sample in timeoutSamples:
            f.write(sample + '\n')

    print("Done")




if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python staticManager.py [path to apk]")
        sys.exit(1)
    runStaticAnalysis(sys.argv[1])