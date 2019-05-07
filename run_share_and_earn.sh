#!/bin/bash

cd /share/CACHEDEV2_DATA/data_process

java -jar StockUtility.jar -start-index 0 -end-index 210 -apply-daily-twse-tpex no -getpage yes -getpage-dividend yes -getpage-earning yes -updatedividend yes -updateearning yes &
java -jar StockUtility.jar -start-index 210 -end-index 420 -apply-daily-twse-tpex no -getpage yes -getpage-dividend yes -getpage-earning yes -updatedividend yes -updateearning yes &
java -jar StockUtility.jar -start-index 420 -end-index 630 -apply-daily-twse-tpex no -getpage yes -getpage-dividend yes -getpage-earning yes -updatedividend yes -updateearning yes &
java -jar StockUtility.jar -start-index 630 -end-index 840 -apply-daily-twse-tpex no -getpage yes -getpage-dividend yes -getpage-earning yes -updatedividend yes -updateearning yes &
java -jar StockUtility.jar -start-index 840 -end-index 1050 -apply-daily-twse-tpex no -getpage yes -getpage-dividend yes -getpage-earning yes -updatedividend yes -updateearning yes &
java -jar StockUtility.jar -start-index 1050 -end-index 1260 -apply-daily-twse-tpex no -getpage yes -getpage-dividend yes -getpage-earning yes -updatedividend yes -updateearning yes &
java -jar StockUtility.jar -start-index 1260 -end-index 1400 -apply-daily-twse-tpex no -getpage yes -getpage-dividend yes -getpage-earning yes -updatedividend yes -updateearning yes &
java -jar StockUtility.jar -start-index 1400 -apply-daily-twse-tpex no -getpage yes -getpage-dividend yes -getpage-earning yes -updatedividend yes -updateearning yes
cd -

