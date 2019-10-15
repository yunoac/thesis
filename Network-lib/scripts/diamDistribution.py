import numpy as np
from pylab import *
import argparse
import os
import json

import utils

if __name__ == '__main__':
  x = [ ]
  for group in utils.listGroups():
    for fn in utils.listGroupInstances(group):
      topology = fn.split('.')[0]
      data = utils.read_data(group, 'diamDeg', topology)
      assert data != None, topology
      v = int(data['edgeDiam'])
      x.append(v)
      print(v)
  #x = utils.data_to_bar(x)
  x.sort()
  print(x)
  x, y = utils.cdf(x, 1)
  plt.plot(x, y)
  plt.show()
  #groups = [ str(i) for i in range(len(x)) ]
  #utils.make_g_barplot([x], groups, ['diameter'], ['#3CAEA3'], 'diameter', 'percentage of topologies', '', '../data/plot/diameter.eps', 5)
