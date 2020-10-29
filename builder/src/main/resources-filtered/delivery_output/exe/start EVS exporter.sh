#!/usr/bin/env bash
cd "$(dirname "$0")"
../system/vault-exporter.sh -transactionType=medicationscheme -patients=katrien -actor=gp_van_gucht -validate=true -exportDir="../exe/exports" -generateGlobalMedicationScheme=true  -generateDailyMedicationScheme=false -generateSumehrOverview=true -generateGatewayMedicationScheme=true -hub=VITALINK -searchType=LOCAL -generateDiaryNoteVisualization=true