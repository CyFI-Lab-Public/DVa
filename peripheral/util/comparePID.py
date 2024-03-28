import sys

def main():
    if len(sys.argv) != 3:
        print("Usage: %s <PID file 1> <PID file 2>" % sys.argv[0])
        print("Outputs the diffrence between two PID files in this format: (USER, PID, PPID, NAME)")
        print("<<< denotes first file, >>> denotes second file")
        return

    pid_file_1 = sys.argv[1]
    pid_file_2 = sys.argv[2]

    file1 = open(pid_file_1, "r")
    file2 = open(pid_file_2, "r")

    set1 = set()
    set2 = set()

    while True:
        line1 = file1.readline()

        if not line1:
            break

        words1 = line1.split()

        set1.add(words1[0]+ " " + words1[1] + " " + words1[2] + " " + words1[8])

    while True:
        line2 = file2.readline()

        if not line2:
            break

        words2 = line2.split()

        set2.add(words2[0]+ " " + words2[1] + " " + words2[2] + " " + words2[8])

    for pid1 in set1:
        if pid1 not in set2:
            print("<<<" + pid1)

    for pid2 in set2:
        if pid2 not in set1:
            print(">>>" + pid2)

    return 0
        


if __name__ == '__main__':
    main()