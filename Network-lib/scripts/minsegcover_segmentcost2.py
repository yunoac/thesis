import numpy as np
from pylab import *
import argparse
import os
import json
import utils

if __name__ == '__main__':
  cost1 = [ ]
  cost2 = [ ]
  for group in utils.listGroups():
    for fn in utils.listGroupInstances(group):
      print(fn)
      topology = fn.split('.')[0]
      data = utils.read_data(group, 'minCover', topology)
      assert data != None, topology
      c = int(data['initialCoverSeg'])
      cost1.append(c)
      data = utils.read_data(group, 'minCover_complete', topology)
      assert data != None, topology
      c = int(data['initialCoverSeg'])
      cost2.append(c)
  print(utils)
  groups = [ str(i) for i in range(max(len(cost1), len(cost2))) ]
  cost1 = utils.data_to_bar(cost1)
  cost2 = utils.data_to_bar(cost2)
  utils.make_g_barplot([cost1, cost2], groups, ['original IGP', 'ECMP-free and complete IGP'], ['#3CAEA3', '#ED553B'], 'segment cost', 'percentage of topologies', '', '../data/plot/minSegCover_segcost_complete.eps', 5)
"""
  ax = plt.subplot()
  plot(x, y)
  plt.xlabel("Topology size |G|")
  plt.ylabel("Runtime in seconds")
  plt.savefig('../data/plot/minSegCover_runtime_by_size_complete.eps', format='eps', dpi=600, bbox_inches='tight')  
"""
