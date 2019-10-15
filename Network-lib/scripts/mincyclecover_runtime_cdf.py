import numpy as np
from pylab import *
import argparse
import os
import json

import utils


if __name__ == '__main__':
  times = [ ]
  for group in utils.listGroups():
    for fn in utils.listGroupInstances(group):
      print(fn)
      topology = fn.split('.')[0]
      data = utils.read_data(group, 'cycleCoverGreedyRounding', topology)
      assert data != None, topology
      t = int(data['runtime'])
      times.append(t / 1e9 / 60)
  print(len(times))
  print(max(times))

  x, y = utils.cdf(times, 0.1)
  plt.plot(x, y)
  plt.xlabel("Runtime of the cycle cover CG algorithm (in minutes)")
  plt.ylabel("Percentage of topologies")
  plt.savefig('../data/plot/minCycleCover_runtime_cdf.eps', format='eps', dpi=600, bbox_inches='tight')  

