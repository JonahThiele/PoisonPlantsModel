#! /bin/bash/

commonNames=("poisonOakAlantic" "angelsTrumpet" "bitterSweetNightShade" "euroBlackElderBerry" "giantHogweed" "hemlock" "jimsonWeed" "Manchineel" "oleander" "poisonOakPacific" "poisonIvy" "poisonSumac" "spurgeNettle" "stingingNettle" "USBlackElderBerry" "whiteBaneBerry" "whiteSnakeRoot" "wildParsnip")
sciNames=("Toxicodendron_pubescens" "Datura_wrightii" "Solanum_dulcamara" "Sambucus_nigra" "Heracleum_maximum" "Conium_maculatum" "Datura_stramonium" "Hippomane_mancinella" "Nerium_oleander" "Toxicodendron_diversilobum" "poisonIvy" "Toxicodendron_vernix" "Cnidoscolus_stimulosus" "Urtica_dioica" "Sambucus_canadensis" "Actaea_pachypoda" "Ageratina_altissima" "Pastinaca_sativa")

for i in {1..35}
do
   command1="mkdir ${commonNames[i]}"
   eval $command1
   command2=" cp json_scraper.py ./${commonNames[i]}"
   eval $command2
   command3="cp parse_json.py ./${commonNames[i]}"
   eval $command3
   command4="cd ${commonNames[i]}"
   eval $command4
   for a in {1..10}
   do
    command5="python3 json_scraper.py --plant ${sciNames[i]} --page $a"
    eval $command5
    command6="python3 parse_json.py --file ${sciNames[i]}${a} --page $a"
    eval $command6
    command7="sh Agrostemma_githago${a}.sh"
    eval $command7
    command8="rm Agrostemma_githago${a}.sh"
    eval $command8
  
   done
    rm json_scraper.py
    rm parse_json.py
    cd ..

done

