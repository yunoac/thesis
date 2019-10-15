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
      c = data['segmentCost']
      V, E = utils.get_graph_size(group, fn)
      cost.append((V + E, c))
  print(cost)
  cost.sort()
  x = [ a for a, _ in cost ]
  y = [ a for _, a in cost ]
  ax = plt.subplot()
  plt.xlabel("Topology size |G|")
  plt.ylabel("Segment cost")
  plt.plot(x, y)
  plt.savefig('../data/plot/minSegCover_segcost_by_size.eps', format='eps', dpi=600, bbox_inches='tight')  
