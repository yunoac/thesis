import numpy as np
from pylab import *
import argparse
import os
import json

import utils


if __name__ == '__main__':
  runtime1 = [ ]
  runtime2 = [ ]
  ratio = [ ]
  skip = [ ]
  for group in utils.listGroups():
    for fn in utils.listGroupInstances(group):
      print(fn)
      topology = fn.split('.')[0]
      data = utils.read_data(group, 'forwPrecompute', topology)
      assert data != None, topology
      t_filter = data['filter']
      t_toposort = data['toposort']
      ratio.append(t_filter / t_toposort)
      runtime1.append(t_filter / 1e9)
      runtime2.append(t_toposort / 1e9)
  x, y = utils.cdf(runtime1, 0.01)
  ax = plt.subplot()
  ax.set_xscale("log")
  plot(x, y, label='filter')
  x, y = utils.cdf(runtime2, 0.01)
  plot(x, y, label='toposort')
  ax.legend()
  """
  x, y = utils.cdf(ratio, 0.01)
  plot(x, y, label='toposort')
  """
  
  plt.xlabel("Runtime in seconds")
  plt.ylabel("Percentage of topologies")
  plt.savefig('../data/plot/precompute_forw_runtime.eps', format='eps', dpi=600, bbox_inches='tight')  

