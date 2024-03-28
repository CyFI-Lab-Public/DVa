import os
import json

def main():
    a11yPath = os.environ['A11Y_PATH']
    dynamicReportPath = os.path.join(a11yPath, "reports/dynamicReports")

    allPermissionDict = {}
    allAdminDict = {}

    totalCount = 0
    escalationCount = 0
    deviceAdminCount = 0
    for (path, subdirs, files) in os.walk(dynamicReportPath):
        for file in files:
            if file.endswith('.json'):
                jsonString = json.loads(open(os.path.join(path, file), 'r').read())
                totalCount += 1
            packageName = jsonString['packageName']
            permissions = jsonString['runtimePermissions']
            if permissions != []:
                escalationCount += 1

            allPermissionDict[packageName] = permissions
            if 'a11yToAdmin' in jsonString.keys():
                if jsonString['a11yToAdmin'] == True:
                    deviceAdminCount += 1
                deviceAdmin = jsonString['a11yToAdmin']
                allAdminDict[packageName] = deviceAdmin
    print("Total apps: " + str(totalCount))
    print("Apps with permission escalation: " + str(escalationCount))
    print("Apps with device admin: " + str(deviceAdminCount))
    # json.dump(allPermissionDict, open(os.path.join(a11yPath, "reports/permissionEscalation.json"), 'w'))
    # json.dump(allAdminDict, open(os.path.join(a11yPath, "reports/deviceAdmin.json"), 'w'))

if __name__ == '__main__':
    main()