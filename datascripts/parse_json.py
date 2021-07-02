import re

matchlist = ""
counter = 0

scraped_file = open(r"poison ivy.json", "r")
for line in scraped_file:
    striped_line = line.strip()
    matchedText = re.search(r"'square_url'", striped_line)
    if matchedText:
        text = 'curl ' + striped_line[15:-2] + ' -o ' + 'poisonIvyImage' + str(counter) + '.jpeg' + '\n'
        counter += 1
        matchlist += text 

curl_file = open( r"poison ivy curl.txt", "w")
curl_file.write(matchlist)
curl_file.close()
