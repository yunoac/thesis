import numpy as np
from pylab import *
import argparse
import os
import json

import utils

if __name__ == '__main__':
  V = [ ]
  E = [ ]
  G = [ ]
  nonSP = 0
  ECMP = 0
  sp_count = [ ]
  min_cut = [ ]
  deg = [ ]
  for group in utils.listGroups():
    for fn in utils.listGroupInstances(group):
      print(group, fn)
      name = fn.split('.')[0]
      data = utils.read_data(group, 'Analysis', name)
      V.append(int(data['V']))
      E.append(int(data['E']))
      G.append(int(data['G']))
      nonSP += float(data['nonSP'])
      ECMP += float(data['ECMP'])
      for c in data['spCount']:
        sp_count.append(int(c))
      for c in data['minCut']:
        min_cut.append(int(c))
      for c in data['degrees']:
        deg.append(int(c))
  n = len(V) 
  small = 0
  medium = 0
  large = 0
  huge = 0
  for v in V:
    if v <= 20:
      small += 1
    elif v <= 50:
      medium += 1
    elif v <= 100:
      large += 1
    else:
      huge += 1
  print(100 * small / n, 100 * medium / n, 100 * large / n, 100 * huge / n)
  print(min(V))
  print(max(V))
  print(min(E))
  print(max(E))
  nonSP /= n
  ECMP /= n
  print('nonSP: ', 100 * nonSP, 'ECMP: ', 100 * ECMP)
  x, y = utils.cdf(sp_count, 1)
  """
  plot(x, y)
  ax = plt.subplot()
  ax.set_xscale("log")
  plt.xlabel("Number of shortest paths")
  plt.ylabel("Percentage of pairs")
  plt.savefig('../data/plot/spCount.eps', format='eps', dpi=600, bbox_inches='tight')  
  """
  x, y = utils.cdf(min_cut, 1)
  print(max(min_cut))
  plot(x, y, label='Min-cut')
  #plt.xticks([1, 2, 3, 4, 5, 6, 7, 8, 10, 46], [1, 2, 3, 4, 5, 6, 7, 8, 10, 46])
  #plt.xlabel("Minimum cut")
  #plt.ylabel("Percentage of pairs")
  #plt.savefig('../data/plot/minCuts.eps', format='eps', dpi=600, bbox_inches='tight')    
  print(max(deg))
  x, y = utils.cdf(deg, 1)
  plt.axvline(x = 46, color = 'k', linestyle='--')
  plt.axvline(x = 67, color = 'k', linestyle='--')
  plot(x, y, label='Degree')
  plt.xticks([1, 2, 3, 4, 5, 10, 15, 46, 67], [1, 2, 3, 4, 5, 10, 15, 46, 67])
  plt.xlabel("Size")
  plt.ylabel("Percentage of pairs")
  plt.legend()
  plt.savefig('../data/plot/degree_mincut.eps', format='eps', dpi=600, bbox_inches='tight')    
