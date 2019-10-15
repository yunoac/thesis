import numpy as np
from pylab import *
import argparse
import os
import json

import utils

def cdf(data, step):
  data.sort()
  count = 0
  x = [ ]
  y = [ ]
  n = len(data)
  value = min(data)
  i = 0
  while i < n:
    if data[i] <= value:
      count += 1
      i += 1
    else:
      x.append(value)
      y.append(count / n)
      value += step
  x.append(data[-1])
  y.append(1)
  return x, [100 * z for z in y]

def read_data(group, topology):
	return json.load(open('../data/results/' + group + '/minLat/' + topology + '.res'))

def latency_gain(group, topology):
  data = read_data(group, topology)
  for res in data:
    if not res['pathExists']: continue

"""	
data = read_data('rf', '1755')['pairResults']
minLat = [ float(x['minLat']) for x in data ]
nomLat = [ float(x['nomLat']) for x in data ]
sr5Lat = [ float(x['sr5Lat']) for x in data ]

x, y = cdf(minLat, 0.01)
plot(x, y)
x, y = cdf(nomLat, 0.01)
plot(x, y)
x, y = cdf(sr5Lat, 0.01)
plot(x, y)

print(np.mean(minLat))
print(np.mean(nomLat))
print(np.mean(sr5Lat))


plt.savefig('test.eps', format='eps', dpi=600, bbox_inches='tight')
"""

def box_plot(data):
  data.sort()
  n = len(data)
  q1 = int(n / 4)
  q3 = int(3 * n / 4)
  median = int(n / 2)
  

if __name__ == '__main__':
  plt.xscale('log')
  labels = [ ]
  M = { }
  zoo_delta = [ ]
  all_delta = [ ]
  for group in utils.listGroups():
    for fn in utils.listGroupInstances(group):
      print(group, fn)
      name = fn.split('.')[0]
      if group != 'zoo': labels.append(name)
      data = read_data(group, name)
      errors = None
      delta_lat = [ ]
      for res in data:
        if res['pathExists']:
          # check for errors
          if len(res['errors']) > 0:
            has_errors = True
            errors = res['errors']
            print(name, res['orig'], res['dest'])
            print(errors)
            exit(0)
          # no errors, gather data
          x = 100 - 100 * float(res['minLat']) / float(res['nomLat'])
          if group == 'zoo':
            zoo_delta.append(x)
          else:
            delta_lat.append(x)
          all_delta.append(x)
      if group != 'zoo':
        M[name] = delta_lat
  M['zoo'] = zoo_delta
  M['all'] = all_delta
  print(np.mean(all_delta))
  # sort labels
  labels.sort()
  labels.append('zoo')
  labels.append('all')
  results = [ M[x] for x in labels ]
  labels = [ utils.topology_name(x) for x in labels ]
  # plot
  fig, ax = plt.subplots()
  bp = ax.boxplot(results, showfliers=False, patch_artist=True)
  print('plotted')
  plt.xticks(range(1, len(labels) + 1), labels, rotation='vertical')
  plt.ylim(-10, 100)
  for e in utils.bp_elems():
    plt.setp(bp[e], color='#1C2331')
  for patch in bp['boxes']:
    patch.set(facecolor='#3F729B')
  plt.setp(bp['medians'], color='#00C851')
  ax.set_title('Latency gain with SR')
  plt.xlabel("Topologies")
  plt.ylabel("Percentage of latency gain")
  plt.savefig('../data/plot/minLat.eps', format='eps', dpi=600, bbox_inches='tight')
