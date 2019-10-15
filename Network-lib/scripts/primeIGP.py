import numpy as np
from pylab import *
import argparse
import os
import json

import utils


if __name__ == '__main__':
  igp = [ ]
  for group in utils.listGroups():
    for fn in utils.listGroupInstances(group):
      print(fn)
      topology = fn.split('.')[0]
      data = utils.read_data(group, 'primeIGP', topology)
      assert data != None, topology
      igp.append(float(data['igp']))
  print(igp)
  x, y = utils.cdf(igp, 1)
  ax = plt.subplot()
  plt.axvline(x=65535, color='k', linestyle='--')
  plot(x, y)
  plt.xlabel("Maximum IGP weight")
  plt.ylabel("Percentage of topologies")
  plt.savefig('../data/plot/primeIGP.eps', format='eps', dpi=600, bbox_inches='tight')  
  print(utils.cdf_percentage(x, y, 65535))
