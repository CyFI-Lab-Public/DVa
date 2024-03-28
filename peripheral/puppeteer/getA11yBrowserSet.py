import json

def main():
    appRankingPath = "/Users/haichuanxu/Desktop/projects/a11y/code/scripts/puppeteer/appBrainRanking.json"
    with open(appRankingPath, 'r') as f:
        appRanking = json.load(f)

    uniqueApps = set()

    categories = ["finance", "transportation", "shopping"]
    countries = ["us"]

    for category in categories:
        for country in countries:
            for app in appRanking[category][country]:
                uniqueApps.add(app)

    print(len(uniqueApps))

    staticSetPath = "/Users/haichuanxu/Desktop/projects/a11yBrowser/staticSet.txt"
    with open(staticSetPath, 'w') as f:
        for app in uniqueApps:
            f.write(app + '\n')

    return 0

if __name__ == "__main__":
    main()