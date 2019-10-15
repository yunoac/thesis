import numpy as np
from pylab import *
import argparse
import os
import json

import utils


if __name__ == '__main__':
  bounds = [ ]
  initial = [ ]
  size = [ ]
  bound_by_size = [ ]
  init_by_size = [ ]
  greedy_by_size = [ ]
  for group in utils.listGroups():
    for fn in utils.listGroupInstances(group):
      print(fn)
      topology = fn.split('.')[0]
      data = utils.read_data(group, 'cycleCoverGreedyRounding', topology)
      assert data != None, topology
      lb = float(data['lpBound'])
      init = int(data['initialCoverSize'])
      greedy = int(data['greedyCoverSize'])
      V, E = utils.get_graph_size(group, fn)
      bounds.append(lb)
      initial.append(init)
      size.append(V + E)
      bound_by_size.append((V + E, lb))
      greedy_by_size.append((V + E, greedy))
      init_by_size.append((V + E, init))

      ub = len(data['values']) + init + 1
      assert lb <= greedy and greedy <= ub
      

  bound_by_size.sort()
  x = [ a for a, _ in bound_by_size ]
  y = [ a for _, a in bound_by_size ]
  plt.plot(x, y, 'o', markersize=2, label='lp bound')

  x = [ a for a, _ in init_by_size ]
  y = [ a for _, a in init_by_size ]
  plt.plot(x, y, 'o', markersize=2, label='min seg cost cover size')

  x = [ a for a, _ in greedy_by_size ]
  y = [ a for _, a in greedy_by_size ]
  plt.plot(x, y, 'o', color='r', markersize=2, label='greedy cover size')

  plt.legend()

  plt.xlabel("Topology size |G|")
  plt.ylabel("Number of cycles")
  plt.savefig('../data/plot/minCycleCover_lowerbound.eps', format='eps', dpi=600, bbox_inches='tight')  
