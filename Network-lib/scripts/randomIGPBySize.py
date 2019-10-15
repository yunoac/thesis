import numpy as np
from pylab import *
import argparse
import os
import json

import utils


if __name__ == '__main__':
  it_size = [ ]
  for group in utils.listGroups():
    for fn in utils.listGroupInstances(group):
      print(fn)
      topology = fn.split('.')[0]
      data = utils.read_data(group, 'randomIGP', topology)
      assert data != None, topology
      V, E = utils.get_graph_size(group, fn)
      it_size.append((V + E, float(data['averageIter'])))
  it_size.sort()
  x = [ a for a, _ in it_size ]
  y = [ a for _, a in it_size ]
  ax = plt.subplot()
  plt.plot([0, max(x)], [0, max(x)], label="x = y", color='k', linestyle='--')
  plot(x, y)
  plt.xlabel("Topology size |G|")
  plt.ylabel("Average number of iterations")
  plt.savefig('../data/plot/randomIGPsize.eps', format='eps', dpi=600, bbox_inches='tight')  
