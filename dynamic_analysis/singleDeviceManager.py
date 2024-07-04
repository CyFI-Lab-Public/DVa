import sys
import os
import time
from subprocess import Popen, PIPE, TimeoutExpired
import json

a11yPath = os.environ['A11Y_PATH']

class DynamicReport:
    def __init__(self):
        self.packageName = ""
        self.mainActivity = ""
        self.a11yServiceNames = []
        self.Errors = []
        self.removedPackages = []
        self.droppedPackages = []
        self.removedSelf = False
        self.runtimePermissions = []
        self.a11yToAdmin = False

    def addError(self, error):
        self.Errors.append(error)

    def dumpReport(self, sample):
        reportPath = os.path.join(a11yPath, 'reports/dynamicReports', sample.strip() + '.json')
        json.dump(self.__dict__, open(reportPath, 'w'))
        print("Finished report for:" + sample)

def main(deviceName):

    hashesPath = os.path.join(a11yPath, 'reports/jobDistribution', deviceName)
    samplesPath = "/Users/haichuanxu/Docs/malware_samples/9850_a11y"

    # get whitelisted package names
    whitelistPath = os.path.join(a11yPath, 'code/scripts/dynamic/debug/whitelistPackages.txt')
    whitelistedPackages = set()
    with open(whitelistPath, 'r') as f:
        for line in f.readlines():
            whitelistedPackages.add(line.strip().split('package:')[1])
    print("Whitelisted packages: " + str(len(whitelistedPackages)))

    # clear initial packages
    initialApps = set()
    listPackagesCommand = "adb -s " + deviceName + " shell pm list packages"
    listPackagesProc = Popen(listPackagesCommand, shell=True, stdout = PIPE, stderr = PIPE)
    try:
        (stdout, stderr) = listPackagesProc.communicate(timeout=3)
    except TimeoutExpired:
        listPackagesProc.kill()
        (stdout, stderr) = listPackagesProc.communicate()
    initialLines = stdout.decode('utf-8').splitlines()
    for line in initialLines:
        if line.startswith('package:'):
            initialApps.add(line.split('package:')[1])
    
    for app in initialApps:
        if app in whitelistedPackages:
            continue
        uninstallCommand  = "adb -s " + deviceName + " uninstall " + app
        uninstallProc = Popen(uninstallCommand, shell=True, stdout = PIPE, stderr = PIPE)
        try:
            (stdout, stderr) = uninstallProc.communicate(timeout=3)
        except TimeoutExpired:
            uninstallProc.kill()
            (stdout, stderr) = uninstallProc.communicate()
        print("Uninstalled: " + app)
    
    print("Finished clearing initial packages on device: " + deviceName)


    # get all samples hashes
    print("Assigning jobs to device: " + deviceName)
    with open(hashesPath, 'r') as f:
        allSamples = f.readlines()
    
    print("Processing: " + str(len(allSamples)) + " samples on device: " + deviceName)

    # start adb processing

    for sample in allSamples:
        report = DynamicReport()
        try:
            samplePath = os.path.join(samplesPath, sample.strip() + '.apk')

            # getting preinstalled applications
            preinstalledApps = set()
            listPackagesCommand = "adb -s " + deviceName + " shell pm list packages"
            listPackagesProc = Popen(listPackagesCommand, shell=True, stdout = PIPE, stderr = PIPE)
            try:
                (stdout, stderr) = listPackagesProc.communicate(timeout=3)
            except TimeoutExpired:
                listPackagesProc.kill()
                (stdout, stderr) = listPackagesProc.communicate()
                report.addError("list preinstalled package timeout")
                report.dumpReport(sample)
                continue
            preinstalledLines = stdout.decode('utf-8').splitlines()
            for line in preinstalledLines:
                if line.startswith('package:'):
                    preinstalledApps.add(line.split('package:')[1])

            print("preinstalled count: " + str(len(preinstalledApps)))

            # install
            installCommand = "adb -s " + deviceName + " install " + samplePath
            print("Installed: " + samplePath)
            installProc = Popen(installCommand, shell=True, stdout = PIPE, stderr = PIPE)
            try:
                (stdout, stderr) = installProc.communicate(timeout=20)
            except TimeoutExpired:
                installProc.kill()
                (stdout, stderr) = installProc.communicate()
                report.addError("install package timeout")
                report.dumpReport(sample)
                

            # get postinstalled applications
            postinstalledApps = set()
            listPackagesProc = Popen(listPackagesCommand, shell=True, stdout = PIPE, stderr = PIPE)
            try:
                (stdout, stderr) = listPackagesProc.communicate(timeout=3)
            except TimeoutExpired:
                listPackagesProc.kill()
                (stdout, stderr) = listPackagesProc.communicate()
                report.addError("list postinstalled package timeout")
                report.dumpReport(sample)
                
            postinstalledLines = stdout.decode('utf-8').splitlines()
            for line in postinstalledLines:
                if line.startswith('package:'):
                    postinstalledApps.add(line.split('package:')[1])

            print("postinstalled count: " + str(len(postinstalledApps)))

            # get installed package name
            installedApp = postinstalledApps - initialApps
            if len(installedApp) != 1:
                report.addError("package install error")
                report.dumpReport(sample)
                continue
                
            packageName = installedApp.pop()
            report.packageName = packageName
            print("package name: " + packageName)

            # get accessibility service names
            dumpSysCommand = "adb -s " + deviceName + " shell dumpsys package " + packageName
            dumpSysProc = Popen(dumpSysCommand, shell=True, stdout = PIPE, stderr = PIPE)
            try:
                (stdout, stderr) = dumpSysProc.communicate(timeout=3)
            except TimeoutExpired:
                dumpSysProc.kill()
                (stdout, stderr) = dumpSysProc.communicate()
                report.addError("dumpsys timeout")
                report.dumpReport(sample)
                
            dumpSysLines = stdout.decode('utf-8').splitlines()
            a11yServiceNames = []
            for line in dumpSysLines:
                if "permission android.permission.BIND_ACCESSIBILITY_SERVICE" in line:
                    a11yServiceNames.append(line.split(packageName + "/")[1].split(' ')[0])
            
            if len(a11yServiceNames) == 0:
                report.addError("no a11y service")
                report.dumpReport(sample)
                
            report.a11yServiceNames = a11yServiceNames
            for name in a11yServiceNames:
                print("a11y service name: " + name)

            # get main activity
            recordMainActivity = False
            for line in dumpSysLines:
                if recordMainActivity:
                    if packageName in line.strip():
                        report.mainActivity = line.strip().split(packageName + "/")[1].split(' ')[0]
                        break
                if line.strip().startswith("android.intent.action.MAIN:"):
                    recordMainActivity = True

            # start main activity
            if report.mainActivity != "":
                startMainActivityCommand = "adb -s " + deviceName + " shell am start -n " + packageName + "/" + report.mainActivity
                startMainActivityProc = Popen(startMainActivityCommand, shell=True, stdout = PIPE, stderr = PIPE)
                try:
                    (stdout, stderr) = startMainActivityProc.communicate(timeout=5)
                except TimeoutExpired:
                    startMainActivityProc.kill()
                    (stdout, stderr) = startMainActivityProc.communicate()
                    report.addError("start main activity timeout")

                print("main activity started: " + report.mainActivity)

            time.sleep(3)
            
            
            # grant a11y permission
            for serviceName in a11yServiceNames:
                grantA11yCommand = "adb -s " + deviceName + " shell settings put secure enabled_accessibility_services " + packageName + "/" + serviceName
                grantA11yProc = Popen(grantA11yCommand, shell=True, stdout = PIPE, stderr = PIPE)
                try:
                    (stdout, stderr) = grantA11yProc.communicate(timeout=3)
                except TimeoutExpired:
                    grantA11yProc.kill()
                    (stdout, stderr) = grantA11yProc.communicate()
                    report.addError("grantA11yPermission timeout")
                    report.dumpReport(sample)
                    
                print("a11y permission granted for: " + packageName + "/" + serviceName)

            # sleep for 5 seconds
            time.sleep(5)
            

            # monitor foreground activity for statbility


            # get posta11y granted permissions
            dumpSysProc = Popen(dumpSysCommand, shell=True, stdout = PIPE, stderr = PIPE)
            try:
                (stdout, stderr) = dumpSysProc.communicate(timeout=3)
            except TimeoutExpired:
                dumpSysProc.kill()
                (stdout, stderr) = dumpSysProc.communicate()
                report.addError("dumpsys timeout")

            dumpSysLines = stdout.decode('utf-8').splitlines()

            def countStartingSpaces(line):
                count = 0
                while count < len(line) and (line[count] == ' '):
                    count += 1
                return count

            startRuntimePermission = False
            numSpaces = -1
            for line in dumpSysLines:
                if startRuntimePermission:
                    if countStartingSpaces(line) != numSpaces + 2:
                        break
                    if "granted=true" in line:
                        report.runtimePermissions.append(line.strip().split(":"))
                if "runtime permissions:" in line:
                    numSpaces = len(line.split("runtime permissions:")[0])
                    startRuntimePermission = True

            print("runtime permissions: " + str(report.runtimePermissions))


            # dynamic analysis


            # get preuninstall package lists
            preUninstalledApps = set()
            listPackagesProc = Popen(listPackagesCommand, shell=True, stdout = PIPE, stderr = PIPE)
            try:
                (stdout, stderr) = listPackagesProc.communicate(timeout=3)
            except TimeoutExpired:
                listPackagesProc.kill()
                (stdout, stderr) = listPackagesProc.communicate()
                report.addError("list preUninstalled package timeout")

            preUninstalledLines = stdout.decode('utf-8').splitlines()
            for line in preUninstalledLines:
                if line.startswith('package:'):
                    preUninstalledApps.add(line.split('package:')[1])
            
            # common packages that malware did not touch
            commonApps = preinstalledApps & preUninstalledApps
            
            malwareDeleteWhitelistPackages = whitelistedPackages - commonApps
            malwareDroppedPackages = preUninstalledApps - commonApps

            # malware removed existing packages
            if (len(malwareDeleteWhitelistPackages)) != 0:
                print("Malware removed existing packages: ")
                for app in malwareDeleteWhitelistPackages:
                    report.removedPackages.append(app)
                    print(app)
                
            # malware removed self
            if packageName not in malwareDroppedPackages:
                print("Malware removed self!")
                report.removedSelf = True
            else:
                malwareDroppedPackages.remove(packageName)
            
            if len(malwareDroppedPackages) != 0:
                print("Malware dropped new packages: ")
                for app in malwareDroppedPackages:
                    report.droppedPackages.append(app)
                    print(app)

                # uninstall additional packages
                for app in malwareDroppedPackages:
                    uninstallCommand = "adb -s " + deviceName + " uninstall " + app
                    print("Uninstalling: " + app)
                    uninstallProc = Popen(uninstallCommand, shell=True, stdout = PIPE, stderr = PIPE)
                    try:
                        (stdout, stderr) = uninstallProc.communicate(timeout=20)
                    except TimeoutExpired:
                        uninstallProc.kill()
                        report.addError("uninstall package timeout")

            # uninstall package
            uninstallCommand = "adb -s " + deviceName + " uninstall " + packageName
            print("Uninstalling: " + packageName)
            uninstallProc = Popen(uninstallCommand, shell=True, stdout = PIPE, stderr = PIPE)
            try:
                (stdout, stderr) = uninstallProc.communicate(timeout=20)
                if ("DELETE_FAILED_DEVICE_POLICY_MANAGER" in stdout.decode('utf-8').strip()):
                    report.a11yToAdmin = True
                    print("ADMIN ESCALATED!")

                    # disable admin and uninstall
                    disableAdminCommand = "adb -s " + deviceName + " shell pm disable-user " + packageName
                    disableAdminProc = Popen(disableAdminCommand, shell=True, stdout = PIPE, stderr = PIPE)
                    try:
                        (stdout, stderr) = disableAdminProc.communicate(timeout=3)
                    except TimeoutExpired:
                        disableAdminProc.kill()
                        report.addError("disable admin timeout")
                    uninstallProc = Popen(uninstallCommand, shell=True, stdout = PIPE, stderr = PIPE)
                    try:
                        (stdout, stderr) = uninstallProc.communicate(timeout=20)
                    except TimeoutExpired:
                        uninstallProc.kill()
                        report.addError("uninstall package timeout")
                    

            except TimeoutExpired:
                uninstallProc.kill()
                (stdout, stderr) = uninstallProc.communicate()
                report.addError("uninstall package timeout")
            


            # return home
            returnHomeCommand = "adb -s " + deviceName + " shell input keyevent KEYCODE_HOME"
            returnHomeProc = Popen(returnHomeCommand, shell=True, stdout = PIPE, stderr = PIPE)
            try:
                (stdout, stderr) = returnHomeProc.communicate(timeout=3)
            except TimeoutExpired:
                returnHomeProc.kill()
                (stdout, stderr) = returnHomeProc.communicate()
                report.addError("return home timeout")

            report.dumpReport(sample)

        except Exception as e:
            report.addError(e)
            report.dumpReport(sample)
    print("Done!")    
    return 0

    
    

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python singleDeviceManager.py [Device Name]")
        sys.exit(1)
    main(sys.argv[1])