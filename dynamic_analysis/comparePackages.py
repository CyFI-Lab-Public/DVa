


def main():
    whitelistPath = "/Users/haichuanxu/Desktop/a11y/code/scripts/dynamic/debug/whitelistPackages.txt"
    debugPath = "/Users/haichuanxu/Desktop/a11y/code/scripts/dynamic/debug/rmAdmin.txt"

    whitelist = set()
    debuglist = set()

    with open(whitelistPath, 'r') as f:
        for line in f:
            whitelist.add(line.strip().split('package:')[1])

    with open(debugPath, 'r') as f:
        for line in f:
            debuglist.add(line.strip().split('package:')[1])
    
    print("Whitelist: " + str(len(whitelist)))
    print("Debuglist: " + str(len(debuglist)))

    commonPackages = whitelist & debuglist
    print("Common packages: " + str(len(commonPackages)))

    deletedPackages = whitelist - commonPackages
    droppedPackages = debuglist - commonPackages

    print("Deleted packages: " + str(len(deletedPackages)))
    for package in deletedPackages:
        print("Deleted package: " + package)

    print("Dropped packages: " + str(len(droppedPackages)))
    for package in droppedPackages:
        print("Dropped package: " + package)

    return 0

if __name__ == "__main__":
    main()