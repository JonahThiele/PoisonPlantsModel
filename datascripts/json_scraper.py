from pyinaturalist import *
from pprint import PrettyPrinter

#constants
plant = 'poison ivy'

response = get_observations(
    taxon_name='Toxicodendron radicans',
    photos=True,
    geo=True,
    geoprivacy='open',
    per_page=1000,
    page=1
)
pprinter = PrettyPrinter()
output_s = pprinter.pformat(response)

with open( plant + '.json', 'w', encoding="utf-8") as file:
    file.write(output_s)
    file.close()
