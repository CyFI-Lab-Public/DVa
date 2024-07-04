import subprocess
import os


def getAVClass():
    AVCLASS_PATH = "/home/haichuan/Documents/tools/avclass"
    A11YPATH = os.environ['A11Y_PATH']
    AVCLASS_REPORT_PATH = os.path.join(A11YPATH, "reports", "avClassReports", "avClassReport0128.txt")

    vtReportPath = os.path.join(A11YPATH, "reports", "vtReports0128")

    with open(AVCLASS_REPORT_PATH, 'w') as f:
        counter = 0
        for path, subdirs, files in os.walk(vtReportPath):
            for file in files:
                if len(file) != 69:
                    continue
                if file[-5:] != ".json":
                    continue

                avClassProcess = subprocess.run(
                    [
                        os.path.join(AVCLASS_PATH, "avclass2", "avclass2_labeler.py"),
                        "-vt",
                        os.path.join(vtReportPath, file),
                        "-c"
                    ],
                    stdout=subprocess.PIPE
                )

                avClassLabel = avClassProcess.stdout.decode("utf-8")

                f.write(file[:-5] + avClassLabel[32:])
                counter += 1
                # if counter == 5:
                #     break

    print("Finished pulling avClass reports. Total reports: ", counter)

if __name__ == "__main__":
    getAVClass()
