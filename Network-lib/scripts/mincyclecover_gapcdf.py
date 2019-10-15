import numpy as np
from pylab import *
import argparse
import os
import json

import utils


if __name__ == '__main__':
  gaps = [ ]
  for group in utils.listGroups():
    for fn in utils.listGroupInstances(group):
      print(fn)
      topology = fn.split('.')[0]
      data = utils.read_data(group, 'cycleCoverGreedyRounding', topology)
      assert data != None, topology
      lb = float(data['lpBound'])
      greedy = int(data['greedyCoverSize'])
      gap = (greedy - lb) / lb
      if gap > 0.98:
        print(greedy, lb, topology)
        exit(0)
      gaps.append(gap)
  print(max(gaps))
  x, y = utils.cdf(gaps, 0.1)
  plt.plot(x, y)
  plt.xlabel("Gap between greedy solution and lower bound")
  plt.ylabel("Percentage of topologies")
  plt.savefig('../data/plot/minCycleCover_gapcdf.eps', format='eps', dpi=600, bbox_inches='tight')  
