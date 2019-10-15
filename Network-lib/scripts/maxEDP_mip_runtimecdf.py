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
      data = utils.read_data(group, 'maxEDPMip', topology)
      assert data != None, topology
      res = data['pairResults']
      for r in res:
        runtime.append(float(r['runtime']) / 1e9)
  x, y = utils.cdf(runtime, 0.01)
  ax = plt.subplot()
  ax.set_xscale("log")
  plot(x, y)
  plt.xlabel("Runtime in seconds")
  plt.ylabel("Percentage of topologies")
  plt.savefig('../data/plot/maxEDPMip_runtime.eps', format='eps', dpi=600, bbox_inches='tight')  
