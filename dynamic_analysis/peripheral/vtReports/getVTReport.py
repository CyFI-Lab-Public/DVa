import requests
import json
import os
import time

# Pathes
a11yPath = os.environ['A11Y_PATH']
allSampleHashesPath = os.path.join(a11yPath, 'reports', 'allSampleHashes.txt')
samplesPath = os.path.join(a11yPath, 'dataset', 'vt_samples')
reportPath = os.path.join(a11yPath, 'reports', 'vtReports0128')



def updateSampleHashes():

    allSampleHashes = set()
    # get all sample hashes
    for path, subdirs, files in os.walk(samplesPath):
        for file in files:
            if len(file) != 64:
                continue
            if file not in allSampleHashes:
                allSampleHashes.add(file)
    
    print("Dataset size: ", len(allSampleHashes))
    
    # update sample hashes
    with open(allSampleHashesPath, 'w') as f:
        for sampleHash in allSampleHashes:
            f.write(sampleHash + '\n')

def getAllSampleHashes():
    allSampleHashes = set()
    with open(allSampleHashesPath, 'r') as f:
        for line in f:
            if len(line) != 65:
                continue
            allSampleHashes.add(line[:-1])
    return allSampleHashes

def getVTReports():

    url = 'https://www.virustotal.com/vtapi/v2/file/report'
    apikey = ''

    # print(response.json())


    # get existing reports

    # updateSampleHashes()
    allSampleHashes = getAllSampleHashes()
    allExistReportHashes = set()

    print("Current dataset size: ", len(allSampleHashes))

    for path, subdirs, files in os.walk(reportPath):
        for file in files:
            if len(file) != 69:
                continue
            if file not in allExistReportHashes:
                allExistReportHashes.add(file[:-5])

    # get missing reports
    missingReportHashes = allSampleHashes - allExistReportHashes

    print("Missing reports size: ", len(missingReportHashes))
    print("Start pulling reports...")

    # pull missing reports

    counter = 0
    for sampleHash in missingReportHashes:
        if (counter >= 950):
            print("Sleeping for 60 seconds...")
            counter = 0
            time.sleep(60)
        counter += 1

        params = {'apikey': apikey, 'resource': sampleHash, 'allinfo': 'true'}
        response = requests.get(url, params=params)

        if response.status_code == 200:
            with open(os.path.join(reportPath, sampleHash + '.json'), 'w') as f:
                json.dump(response.json(), f)
        else:
            print("Error: ", response.status_code, sampleHash)


        # with open('vt_report.json', 'w') as f:
        #     json.dump(response.json(), f, indent=4)

    print("Done!")



if __name__ == '__main__':
    getVTReports()

    