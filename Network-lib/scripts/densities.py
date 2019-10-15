import numpy as np
from pylab import *
import argparse
import os
import json

import utils

def get_size(V):
  if V <= 20:
    return 'small'
  elif V <= 50:
    return 'medium'
  elif V <= 100:
    return 'large'
  else:
    return 'huge'
  

if __name__ == '__main__':
  den = [ ]
  for group in utils.listGroups():
    for fn in utils.listGroupInstances(group):
      print(group, fn)
      name = fn.split('.')[0]
      data = utils.read_data(group, 'Analysis', name)
      V = float(data['V'])
      E = float(data['E'])
      den.append(E / (V * (V - 1)))
  x, y = utils.cdf(den, 0.01)
  plot(x, y, label='Edge density')
  plt.xticks([0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1])
  plt.xlabel("Edge density")
  plt.ylabel("Percentage of topologies")
  plt.legend()
  plt.savefig('../data/plot/edge_density.eps', format='eps', dpi=600, bbox_inches='tight')    
