import numpy as np
from pylab import *
import argparse
import os
import json
import utils

if __name__ == '__main__':
  ratios = [ ]
  for group in utils.listGroups():
    for fn in utils.listGroupInstances(group):
      print(fn)
      topology = fn.split('.')[0]
      data = utils.read_data(group, 'identificationCost', topology)
      assert data != None, topology
      c1 = float(data['originalCover']['runtime'])
      c2 = float(data['newCover']['runetime'])
      ratios.append(c2 / c1)
  x, y = utils.cdf(ratios, 0.01)
  plot(x, y)
  plt.show()
"""
  ax = plt.subplot()
  plot(x, y)
  plt.xlabel("Topology size |G|")
  plt.ylabel("Runtime in seconds")
  plt.savefig('../data/plot/minSegCover_runtime_by_size_complete.eps', format='eps', dpi=600, bbox_inches='tight')  
"""
