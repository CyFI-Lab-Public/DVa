import sys
import os
import subprocess


def main():
    # a11yPath = os.environ['A11Y_PATH']
    a11yPath = "."

    # get all samples hashes
    samplesPath = "./samples"
    allSamples = []
    for (path, subdirs, files) in os.walk(samplesPath):
        for file in files:
            if not file.endswith('.apk'):
                continue
            allSamples.append(file.split('.')[0])

    print("Total samples: " + str(len(allSamples)))

    # organize sample results by hash 
    # eliminate ones already processed
    dynamicReportPath = os.path.join(a11yPath, "reports/dynamicReports")

    for (path, subdirs, files) in os.walk(dynamicReportPath):
        for file in files:
            if file.split('.')[0] in allSamples:
                allSamples.remove(file.split('.')[0])
        break

    print("Need to process: " + str(len(allSamples)))

    # get all devices
    devices = []
    adbDeviceOutput = subprocess.check_output(["adb", "devices"])
    for line in adbDeviceOutput.splitlines():
        if len(line) == 0:
            continue
        if line.startswith(b'List'):
            continue
        if line.startswith(b'*'):
            continue
        if line.startswith(b' '):
            continue
        if line.startswith(b'offline'):
            continue
        if line.startswith(b'unauthorized'):
            continue
        if line.startswith(b'device'):
            continue
        devices.append(line.decode('utf-8').split('\t')[0])
    print(str(len(devices)) + " available devices")
    print("Devices: " + str(devices))

    # assign jobs to devices
    deviceJobs = {}
    jobsPerDevice = int(len(allSamples) / len(devices))
    for i in range(len(devices)-1):
        deviceJobs[devices[i]] = allSamples[i * jobsPerDevice: (i + 1) * jobsPerDevice]
    deviceJobs[devices[-1]] = allSamples[(len(devices) - 1) * jobsPerDevice:]

    print("Jobs per device: " + str(jobsPerDevice))
    totalAssignedJobs = 0
    for device in deviceJobs:
        totalAssignedJobs += len(deviceJobs[device])

    jobDistributionPath = os.path.join(a11yPath, "reports/jobDistribution")

    ### write jobs to files ###
    
    for deviceJob in deviceJobs:
        with open(os.path.join(jobDistributionPath, deviceJob), 'w') as f:
            # test with 100 samples per device
            # count = 0
            for sample in deviceJobs[deviceJob]:
                # if count >= 100:
                #     break
                f.write(sample + '\n')
                # count += 1

    print("Total assigned jobs: " + str(totalAssignedJobs))



    ###### run single device dynamic analysis manager ######
    cmdsList = [['python3', "singleDeviceManager.py", deviceName] for deviceName in devices]

    procs = []
    for cmd in cmdsList:
        proc = subprocess.Popen(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        procs.append(proc)

    while True:
        if procs == []:
            break
        for proc in procs:
            # output = proc.stdout.readline()
            if proc.poll() is not None:
                (stdout, stderr) = proc.communicate()
                # print(stdout.decode('utf-8'))
                procs.remove(proc)
                print("Process " + str(proc.pid) + " finished")
                continue
            # if output:
            #     print(output.strip().decode('utf-8'))
    
    return 0
        


if __name__ == '__main__':
    main()