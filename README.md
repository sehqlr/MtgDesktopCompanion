![https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/src/main/resources/icons/logo_src.png](https://raw.githubusercontent.com/nicho92/MtgDesktopCompanion/master/src/main/resources/icons/logo_src.png)

# Magic The Gathering Companion
Personal Magic the Gathering card manager Deck Builder and Collection Editor

[![GitHub issues](https://img.shields.io/github/issues/nicho92/MtgDesktopCompanion.svg)](https://github.com/nicho92/MtgDesktopCompanion/issues)
[![Build Status](https://travis-ci.org/nicho92/MtgDesktopCompanion.svg?branch=master)](https://travis-ci.org/nicho92/MtgDesktopCompanion)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=org.magic%3Amagic-api&metric=alert_status)](https://sonarcloud.io/dashboard?id=org.magic%3Amagic-api)
[![GitHub forks](https://img.shields.io/github/forks/nicho92/MtgDesktopCompanion.svg)](https://github.com/nicho92/MtgDesktopCompanion/network)
[![GitHub stars](https://img.shields.io/github/stars/nicho92/MtgDesktopCompanion.svg)](https://github.com/nicho92/MtgDesktopCompanion/stargazers)
[![GitHub stars](https://img.shields.io/twitter/url/https/shields.io.svg?style=social)](https://twitter.com/mtgdesktopcomp1)
[![PayPal](https://img.shields.io/static/v1.svg?label=PayPal&message=Support%20MTGCompanion&color=Blue&logo=paypal)](https://www.paypal.me/nicolaspihen)


# Website : 
[MTG Companion website](https://www.MtgCompanion.org/)

[![GitHub stars](https://img.shields.io/badge/download-2.25-green.svg)](https://github.com/nicho92/MtgDesktopCompanion/releases/)

# Launch

>Need to have Java >=15 installed : https://www.oracle.com/technetwork/java/javase/downloads/index.html
>
>download and unzip latest release at https://github.com/nicho92/MtgDesktopCompanion/releases
>
>go to /bin directory and launch mtg-desktop-companion.bat (for windows) or mtg-desktop-companion.sh (for unix)


# Setup from source
```
git clone https://github.com/nicho92/MtgDesktopCompanion.git

mvn -DskipTests clean install

cd target/executable/bin and launch mtg-desktop-companion.bat or mtg-desktop-companion.sh

```

# Features :

- Multi Engine : Scryfall, MTGJson,...
- Multi Database : MySQL, Postgres, Hsql, MongoDB,...
- Deck Editor (construct, sealed) and import tool from many websites (tappedout, deckstat,mtggoldfish,mtgTop8,...)
- Collection manager
- Prices analysis from many providers  (MTGStock, MTGOldfish,...)
- import / export decks and list cards to dozen formats (mtgo,dci sheet, csv, cockatrice,MagicCardMarket wantlist..) 
- Cards prices alerts
- Manacurve, colors and types repartition analysis
- Standalone servers (game room, console server, http server, price checking).
- New magiccardMarket Pricer : Stay tunned !!,  when you're alerted by a good bid for your wanted cards, it's automatically added to your cart's account ! 
- Manage your stock card, mass modification, import/export from deck, website. Update your Mkm Seller Account stock, Automaticaly update prices !
- Get alerted with many notifier (Telegram, mail, Discord,....) 
- Cross-plateform : Discord Bot, Plugin for Chrome,...
- Embedded webUI and JsonServer
- Embedded webshop server


# Portfolio

Main interface :
![https://www.mtgcompanion.org/img/portfolio/fullsize/1.png](https://www.mtgcompanion.org/img/portfolio/fullsize/1.png)


![https://www.mtgcompanion.org/img/portfolio/fullsize/magicThumbnail.png](https://www.mtgcompanion.org/img/portfolio/fullsize/magicThumbnail.png)


Get realtime prices on seller websites :
![https://www.mtgcompanion.org/img/portfolio/fullsize/cardsprices.png](https://www.mtgcompanion.org/img/portfolio/fullsize/cardsprices.png)


Deck Manager :
![https://www.mtgcompanion.org/img/portfolio/fullsize/deckManager.png](https://www.mtgcompanion.org/img/portfolio/fullsize/deckManager.png)
![https://www.mtgcompanion.org/img/portfolio/fullsize/deckManager2.png](https://www.mtgcompanion.org/img/portfolio/fullsize/4.png)
![https://www.mtgcompanion.org/img/portfolio/fullsize/import_deck.png](https://www.mtgcompanion.org/img/portfolio/fullsize/import_deck.png)


Collection Manager :
![https://www.mtgcompanion.org/img/portfolio/fullsize/collectionManager.png](https://www.mtgcompanion.org/img/portfolio/fullsize/6.png)

Manage your dashboard with your interested "dashlet"
![https://www.mtgcompanion.org/img/portfolio/fullsize/dashboard.png](https://www.mtgcompanion.org/img/portfolio/fullsize/dashboard.png)

Check for price variation :
![https://www.mtgcompanion.org/img/portfolio/fullsize/priceVariation.png](https://www.mtgcompanion.org/img/portfolio/fullsize/priceVariation.png)

Use "MoreLikeThis" Functionnality :
![https://www.mtgcompanion.org/img/portfolio/fullsize/morelikethis.png](https://www.mtgcompanion.org/img/portfolio/fullsize/morelikethis.png)

import or export your card list / deck :
![https://www.mtgcompanion.org/img/portfolio/fullsize/import_export.png](https://www.mtgcompanion.org/img/portfolio/fullsize/import_export.png)
![https://www.mtgcompanion.org/img/portfolio/fullsize/webcam.png](https://www.mtgcompanion.org/img/portfolio/fullsize/webcam.png)

Be alerted for your wanted cards :
![https://www.mtgcompanion.org/img/portfolio/fullsize/alerts.png](https://www.mtgcompanion.org/img/portfolio/fullsize/5.png)

Manage your stock :
![https://www.mtgcompanion.org/img/portfolio/fullsize/stockManagement.png](https://www.mtgcompanion.org/img/portfolio/fullsize/stockManagement.png)
![https://www.mtgcompanion.org/img/portfolio/fullsize/stock.png](https://www.mtgcompanion.org/img/portfolio/fullsize/stock.png)

Store your financial movement and check your benefits :
![https://www.mtgcompanion.org/img/portfolio/fullsize/financial.png](https://www.mtgcompanion.org/img/portfolio/fullsize/financial.png)

Try your deck in game simulator :
![https://www.mtgcompanion.org/img/portfolio/fullsize/tryDeck.png](https://www.mtgcompanion.org/img/portfolio/fullsize/tryDeck.png)

Create deck in sealed format :
![https://www.mtgcompanion.org/img/portfolio/fullsize/sealed.png](https://www.mtgcompanion.org/img/portfolio/fullsize/3.png)

get your companion in discord's channels :
![https://www.mtgcompanion.org/img/portfolio/fullsize/discord.png](https://www.mtgcompanion.org/img/portfolio/fullsize/discord.png)

get your companion in chrome :
![https://www.mtgcompanion.org/img/portfolio/fullsize/mtgchromecompanion.png](https://www.mtgcompanion.org/img/portfolio/fullsize/mtgchromecompanion.png)

Publish your collection throught your embedded shopping website :
![https://www.mtgcompanion.org/img/portfolio/fullsize/webshop.png](https://www.mtgcompanion.org/img/portfolio/fullsize/webshop.png)


Generate Website of your collections :
![https://www.mtgcompanion.org/img/portfolio/fullsize/website.png](https://www.mtgcompanion.org/img/portfolio/fullsize/website.png)
![https://www.mtgcompanion.org/img/portfolio/fullsize/website2.png](https://www.mtgcompanion.org/img/portfolio/fullsize/website2.png)

Create your own cards :
![https://www.mtgcompanion.org/img/portfolio/fullsize/cardmaker.png](https://www.mtgcompanion.org/img/portfolio/fullsize/cardmaker.png)

Get news from your favorites sites :
![https://www.mtgcompanion.org/img/portfolio/fullsize/news.png](https://www.mtgcompanion.org/img/portfolio/fullsize/news.png)

Manage your card with the reponsive web UI :
![https://www.mtgcompanion.org/img/portfolio/fullsize/web-ui.png](https://www.mtgcompanion.org/img/portfolio/fullsize/2.png)
![https://www.mtgcompanion.org/img/portfolio/fullsize/web-ui-2.png](https://www.mtgcompanion.org/img/portfolio/fullsize/web-ui-2.png)
