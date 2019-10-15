import numpy as np
from pylab import *
import argparse
import os
import json

import utils


if __name__ == '__main__':
  gap = [ ]
  for group in utils.listGroups():
    for fn in utils.listGroupInstances(group):
      print(fn)
      topology = fn.split('.')[0]
      data = utils.read_data(group, 'cycleCoverGreedyRounding', topology)
      assert data != None, topology
      lb = float(data['lpBound'])
      greedy = int(data['greedyCoverSize'])
      gap.append(100 * (greedy - lb) / lb)
  print(gap)
  x, y = utils.cdf(gap, 0.1)
  plt.plot(x, y)
  plt.xlabel("Difference between greedy solution and lower bound")
  plt.ylabel("Percentage of topologies")
  plt.savefig('../data/plot/minCycleCover_gap.eps', format='eps', dpi=600, bbox_inches='tight')  
