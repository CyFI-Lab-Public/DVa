import json

def main():
    appRankingPath = "/Users/haichuanxu/Desktop/a11y/code/scripts/puppeteer/appBrainRanking.json"
    with open(appRankingPath, 'r') as f:
        appRanking = json.load(f)
    
    uniqueApps = set()
    for category in appRanking:
        for country in appRanking[category]:
            for app in appRanking[category][country]:
                uniqueApps.add(app)

    print(len(uniqueApps))

    curVictims = ["com.bankinter.launcher", "com.bbva.bbvacontigo", "com.cajasur.android", "com.grupocajamar.wefferent", "com.imaginbank.app", "com.kutxabank.android", "com.rsi", "com.tecnocom.cajalaboral", "es.bancosantander.apps", "es.cm.android", "es.evobanco.bancamovil", "es.ibercaja.ibercajaapp", "es.liberbank.cajasturapp", "es.openbank.mobile", "es.pibank.customers", "es.unicajabanco.app", " www.ingdirect.nativeframe", "com.binance.dev", "com.coinbase.android"]

    counter = 0
    for curVictim in curVictims:
        if curVictim in uniqueApps:
            print(curVictim)
            counter += 1
    print(str(counter) + '/' + str(len(curVictims)))

    return 0 
if __name__ == "__main__":
    main()