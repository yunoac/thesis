import numpy as np
from pylab import *
import argparse
import os
import json

import utils


if __name__ == '__main__':
  it = [ ]
  for group in utils.listGroups():
    for fn in utils.listGroupInstances(group):
      print(fn)
      topology = fn.split('.')[0]
      data = utils.read_data(group, 'randomIGP', topology)
      assert data != None, topology
      it.append(float(data['averageIter']))
  x, y = utils.cdf(it, 1)
  ax = plt.subplot()
  plot(x, y)
  plt.xlabel("Number of iterations")
  plt.ylabel("Percentage of topologies")
  plt.savefig('../data/plot/randomIGP.eps', format='eps', dpi=600, bbox_inches='tight')
