import numpy as np
from pylab import *
import argparse
import os
import json

import utils

if __name__ == '__main__':
  cost = [ ]
  for group in utils.listGroups():
    for fn in utils.listGroupInstances(group):
      print(fn)
      topology = fn.split('.')[0]
      data = utils.read_data(group, 'minSegCover', topology)
      assert data != None, topology
      c = int(data['segmentCost'])
      cost.append(c)
  cost = utils.count(cost)
  cost = utils.dict_to_bar(cost)
  cost = utils.array_to_percent(cost)
  print(cost)
  groups = [ str(i) for i in range(len(cost)) ]
  utils.make_g_barplot([cost], groups, ['seg cost'], ['#3CAEA3'], 'segment cost', 'percentage of topologies', '', '../data/plot/minSegCover_segcost.eps', 5)
"""
  ax = plt.subplot()
  plot(x, y)
  plt.xlabel("Topology size |G|")
  plt.ylabel("Runtime in seconds")
  plt.savefig('../data/plot/minSegCover_runtime_by_size.eps', format='eps', dpi=600, bbox_inches='tight')  
"""
