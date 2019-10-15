import numpy as np
from pylab import *
import argparse
import os
import json

import utils


if __name__ == '__main__':
  s = [ ]
  for group in utils.listGroups():
    for fn in utils.listGroupInstances(group):
      print(fn)
      topology = fn.split('.')[0]
      data = utils.read_data(group, 'primeIGP', topology)
      assert data != None, topology
      s.append(int(data['s']))
  print(s)
  s = utils.data_to_bar(s)
  groups = [ str(i) for i in range(len(s)) ]
  utils.make_g_barplot([s], groups, ['s'], ['#3CAEA3'], 'value of s', 'percentage of topologies', '', '../data/plot/primeIGP_s.eps', 5)
