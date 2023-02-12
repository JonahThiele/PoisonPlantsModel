from pyinaturalist import *
from pprint import PrettyPrinter
import argparse

parser = argparse.ArgumentParser()

# use the _ instead of spaces
parser.add_argument('--plant', type=str, required=True)

parser.add_argument('--page', type=int, required=True)

args = parser.parse_args()

#constants
plant = args.plant

# if plant has underscore replace with space
for i in range(0, len(plant)):
    if( plant[i] == '_'):
        plant = plant[:i] + " " + plant[i + 1:]
print(plant)
response = get_observations(
    taxon_name=plant,
    photos=True,
    geo=True,
    geoprivacy='open',
    per_page=200,
    page=args.page
)
pprinter = PrettyPrinter()
output_s = pprinter.pformat(response)

with open( args.plant + str(args.page) + '.json', 'w', encoding="utf-8") as file:
    file.write(output_s)
    file.close()
