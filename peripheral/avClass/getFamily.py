import os

def getFamily():
    A11YPATH = os.environ['A11Y_PATH']
    AVCLASS_REPORT_PATH = os.path.join(A11YPATH, "reports", "avClassReports", "avClassReport0128.txt")
    FAMILY_PATH = os.path.join(A11YPATH, "reports", "avClassReports", "family0128.txt")

    familyDict = {}
    with open(AVCLASS_REPORT_PATH, 'r') as f:
        for line in f:
            family = line.split("\t")[1].split("\n")[0]
            if "SINGLETON" in family:
                family = "SINGLETON"
            if family not in familyDict:
                familyDict[family] = 1
            else:
                familyDict[family] += 1
    sortedDict = sorted(familyDict.items(), key=lambda x: x[1], reverse=True)
    
    with open(FAMILY_PATH, 'w') as f:
        for key, value in sortedDict:
            f.write(key + " " + str(value) + "\n")
    
    counter = 0
    print("Top 10 Families:")
    for key, value in sortedDict:
        if counter == 11:
            break
        print(key, value)
        counter += 1
    
    totalSamples = 0
    for key, value in sortedDict:
        totalSamples += value
    
    print("Total samples: ", totalSamples)

if __name__ == "__main__":
    getFamily()