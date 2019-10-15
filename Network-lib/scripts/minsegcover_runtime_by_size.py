import numpy as np
from pylab import *
import argparse
import os
import json

import utils

if __name__ == '__main__':
  runtime = [ ]
  for group in utils.listGroups():
    for fn in utils.listGroupInstances(group):
      print(fn)
      topology = fn.split('.')[0]
      data = utils.read_data(group, 'minSegCover', topology)
      assert data != None, topology
      time = float(data['runtime']) / 1e9
      V, E = utils.get_graph_size(group, fn)
      runtime.append((V + E, time))
  runtime.sort()
  x = [ a for a, _ in runtime ]
  y = [ a for _, a in runtime ]
  ax = plt.subplot()
  plot(x, y)
  plt.xlabel("Topology size |G|")
  plt.ylabel("Runtime in seconds")
  plt.savefig('../data/plot/minSegCover_runtime_by_size.eps', format='eps', dpi=600, bbox_inches='tight')  
