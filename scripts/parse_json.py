import re
import argparse

parser = argparse.ArgumentParser()

parser.add_argument('--file', type=str, required=True)
parser.add_argument('--page', type=int, required=True)

args = parser.parse_args()


matchlist = "#!/bin/bash\nIFS=$' \t\r\n'\n"
counter = 0
in_observation_photos = False

scraped_file = open(args.file+".json", "r")
for line in scraped_file:
    striped_line = line.strip()
    matchedText = re.search(r"'observation_photos'", striped_line)
    matchedUrl = re.search(r"'medium_url'", striped_line)
    if matchedText:
        in_observation_photos = True
    if(matchedUrl and in_observation_photos): 
        if(striped_line[-1] == "'" or "}"):
            striped_line = striped_line.rstrip(striped_line[-1])
        text = 'curl ' + 'https:/' + striped_line[15:-2] + ' -o ' + 'poisonIvyImage' +  str(args.page) + str(counter) + '.jpeg' + '\n'
        counter += 1
        matchlist += text
        in_observation_photos = False 

curl_file = open( args.file+".sh", "w")
curl_file.write(matchlist)
curl_file.close()