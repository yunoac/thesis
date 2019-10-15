import numpy as np
from pylab import *
import argparse
import os
import json

import utils


if __name__ == '__main__':
  bound_by_size = [ ]
  init_by_size = [ ]
  bound_by_size_complete = [ ]
  init_by_size_complete = [ ]

  lb_ratios = [ ]
  for group in utils.listGroups():
    for fn in utils.listGroupInstances(group):
      if not fn.endswith('.json'): continue
      print(fn)
      V, E = utils.get_graph_size(group, fn)

      topology = fn.split('.')[0]
      data = utils.read_data(group, 'minCover', topology)
      assert data != None, topology
      
      lb1 = float(data['lpBound'])
      init = int(data['initialCoverSize'])
      bound_by_size.append((V + E, lb1))
      init_by_size.append((V + E, init))

      data = utils.read_data(group, 'minCover_complete', topology)
      assert data != None, topology

      lb2 = float(data['lpBound'])
      init = int(data['initialCoverSize'])
      bound_by_size_complete.append((V + E, lb2))
      init_by_size_complete.append((V + E, init))
      lb_ratios.append(lb1 / lb2)

 
  x = [ a for a, _ in bound_by_size ]
  y = [ a for _, a in bound_by_size ]
  plt.plot(x, y, 'o', markersize=1, label='lp bound')

  x = [ a for a, _ in init_by_size ]
  y = [ a for _, a in init_by_size ]
  plt.plot(x, y, 'o', markersize=1, label='min seg cost')

  x = [ a for a, _ in init_by_size_complete ]
  y = [ a for _, a in init_by_size_complete ]
  plt.plot(x, y, 'o', markersize=1, label='min seg cost igp-complete')

  x = [ a for a, _ in bound_by_size_complete ]
  y = [ a for _, a in bound_by_size_complete ]
  plt.plot(x, y, 'o', markersize=1, label='lp bound igp-complete')

  plt.legend()

  plt.xlabel("Number of cycles")
  plt.ylabel("Topology size |G|")
  plt.savefig('../data/plot/minCycleCover_lowerbound_complete.eps', format='eps', dpi=600, bbox_inches='tight')  

