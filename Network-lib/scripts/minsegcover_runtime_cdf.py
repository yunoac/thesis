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
      runtime.append(time)
  x, y = utils.cdf(runtime, 0.01)
  ax = plt.subplot()
  ax.set_xscale("log")
  plot(x, y)
  plt.xlabel("Runtime in seconds")
  plt.ylabel("Percentage of topologies")
  plt.savefig('../data/plot/minSegCover_runtime.eps', format='eps', dpi=600, bbox_inches='tight')  
