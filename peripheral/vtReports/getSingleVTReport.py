import os
import requests
import json
import sys
import subprocess

def getSingleVTReport(hash):
    url = 'https://www.virustotal.com/vtapi/v2/file/report'
    apikey = ''
    params = {'apikey': apikey, 'resource': hash, 'allinfo': 'true'}
    response = requests.get(url, params=params)

    a11yPath = os.environ['A11Y_PATH']
    vtReportPath = os.path.join(a11yPath, 'reports', 'debug', 'vtReport_' + hash + '.json')

    if response.status_code == 200:
        with open(vtReportPath, 'w') as f:
            json.dump(response.json(), f)
    else:
        print("Error: ", response.status_code, hash)
    print("VT Report saved to: ", vtReportPath)

    AVCLASS_PATH = "/home/haichuan/Documents/tools/avclass"

    avClassProcess = subprocess.run(
        [
            os.path.join(AVCLASS_PATH, "avclass2", "avclass2_labeler.py"),
            "-vt",
            vtReportPath,
            "-c"
        ],
        stdout=subprocess.PIPE
    )

    avClassLabel = avClassProcess.stdout.decode("utf-8")

    print(avClassLabel)


if __name__ == '__main__':
    if len(sys.argv) != 2:
        print("Usage: python getSingleVTReport.py <hash>")
        exit(1)
    getSingleVTReport(sys.argv[1])