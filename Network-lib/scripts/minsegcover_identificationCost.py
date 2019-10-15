import numpy as np
from pylab import *
import argparse
import os
import json
import utils

if __name__ == '__main__':
  cost1 = [ ]
  for group in utils.listGroups():
    for fn in utils.listGroupInstances(group):
      print(fn)
      topology = fn.split('.')[0]
      data = utils.read_data(group, 'identificationCost', topology)
      assert data != None, topology
      c = int(max(data['originalIGP']))
      cost1.append(c)
  groups = [ str(i) for i in range(len(cost1)) ]
  cost1 = utils.data_to_bar(cost1)
  utils.make_g_barplot([cost1], groups, ['original IGP'], ['#3CAEA3'], 'maximum identification sr-cylce segment cost', 'percentage of topologies', '', '../data/plot/minSegCover_identification_orig.eps', 50)
"""
  ax = plt.subplot()
  plot(x, y)
  plt.xlabel("Topology size |G|")
  plt.ylabel("Runtime in seconds")
  plt.savefig('../data/plot/minSegCover_runtime_by_size_complete.eps', format='eps', dpi=600, bbox_inches='tight')  
"""
