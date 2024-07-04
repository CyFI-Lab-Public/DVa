import os
import sys
from subprocess import Popen, PIPE, TimeoutExpired, check_output

def main():

    # get all devices
    devices = []
    adbDeviceOutput = check_output(["adb", "devices"])
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

    # get whitelist
    a11yPath = os.environ['A11Y_PATH']
    whitelistPath = os.path.join(a11yPath, 'code/scripts/dynamic/debug/whitelistPackages.txt')
    whitelistedPackages = set()
    with open(whitelistPath, 'r') as f:
        for line in f.readlines():
            whitelistedPackages.add(line.strip().split('package:')[1])
    print("Whitelisted packages: " + str(len(whitelistedPackages)))


    # clear each device
    for deviceName in devices:
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
        
        for packageName in initialApps:
            if packageName in whitelistedPackages:
                continue
                        # uninstall package
            uninstallCommand = "adb -s " + deviceName + " uninstall " + packageName
            print("Uninstalling: " + packageName)
            uninstallProc = Popen(uninstallCommand, shell=True, stdout = PIPE, stderr = PIPE)
            try:
                (stdout, stderr) = uninstallProc.communicate(timeout=20)
                if ("DELETE_FAILED_DEVICE_POLICY_MANAGER" in stdout.decode('utf-8').strip()):
                    print("Deleting admin app")

                    # disable admin and uninstall
                    disableAdminCommand = "adb -s " + deviceName + " shell pm disable-user " + packageName
                    disableAdminProc = Popen(disableAdminCommand, shell=True, stdout = PIPE, stderr = PIPE)
                    try:
                        (stdout, stderr) = disableAdminProc.communicate(timeout=3)
                    except TimeoutExpired:
                        disableAdminProc.kill()
                    uninstallProc = Popen(uninstallCommand, shell=True, stdout = PIPE, stderr = PIPE)
                    try:
                        (stdout, stderr) = uninstallProc.communicate(timeout=20)
                    except TimeoutExpired:
                        uninstallProc.kill()
            except TimeoutExpired:
                uninstallProc.kill()
                (stdout, stderr) = uninstallProc.communicate()
        
        print("Finished clearing initial packages on device: " + deviceName)
    print("Finished clearing initial packages on all devices")


if __name__ == "__main__":
    main()