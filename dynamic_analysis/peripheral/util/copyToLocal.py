import os
import subprocess

SHA256LENGTH = 64
APKLENGTH = 4

def copyToLocal():
    A11Y_PATH = os.environ['A11Y_PATH']
    NAS_PATH  = os.path.join(A11Y_PATH, 'dataset', 'vt_samples')
    LOCAL_PATH = os.path.join(A11Y_PATH, 'local_dataset')

    # existing samples
    existingSamples = set()
    for path, subdirs, files in os.walk(LOCAL_PATH):
        for file in files:
            if len(file) != SHA256LENGTH + APKLENGTH:
                continue
            existingSamples.add(file[:-APKLENGTH])
    
    print("Existing samples: ", len(existingSamples))

    # copy nas samples to local
    counter = 0
    for path, subdirs, files in os.walk(NAS_PATH):
        for file in files:
            if len(file) != SHA256LENGTH:
                continue
            if file in existingSamples:
                continue
            subprocess.run(
                [
                    'cp',
                    os.path.join(path, file),
                    os.path.join(LOCAL_PATH, file + '.apk')
                ]
            )
            counter += 1


    print("Finished copying samples. Total samples: ", counter)



if __name__ == '__main__':
    copyToLocal()