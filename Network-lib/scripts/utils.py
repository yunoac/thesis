import os

def listGroups():
  #return os.listdir('../data/topologies')
  return ['zoo', 'rf', 'real', 'ovh']

def listGroupInstances(group):
  l = os.listdir('../data/topologies/' + group)
  ll = [ ]
  for fn in l:
    if fn.endswith('.json'):
      ll.append(fn)
  return ll

def topology_name(name):
  if name == 'real1': return 'real1'
  if name == 'real2': return 'real2'
  if name == 'real3': return 'real3'
  if name == '1239': return 'AS1239'
  if name == '1221': return 'AS1221'
  if name == '1755': return 'AS1755'
  if name == '3257': return 'AS3257'
  if name == '3967': return 'AS3967'
  if name == '6461': return 'AS6461'
  return name

def bp_elems():
  return ['boxes', 'whiskers', 'fliers', 'means', 'medians', 'caps']

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

def cdf_percentage(x, y, x_val):
  n = len(x)
  y_val = 0
  for i in range(n):
    if x[i] < x_val:
      y_val = y[i]
  return y_val

def count(data):
  d = { }
  for x in data:
    if not x in d:
      d[x] = 0
    d[x] += 1
  return d

def percentages(data):
  d = { }
  for x in data:
    if not x in d:
      d[x] = 0
    d[x] += 1
  for k in d:
    d[k] = 100 * d[k] / len(data)
  return d

def read_data(group, experiment, topology):
  import json
  print('../data/results/' + group + '/' + experiment + '/' + topology + '.json')
  if os.path.exists('../data/results/' + group + '/' + experiment + '/' + topology + '.json'):
    return json.load(open('../data/results/' + group + '/' + experiment + '/' + topology + '.json'))
  return None

def read_json(fn):
  import json
  return json.load(open(fn))

def get_graph_size(group, topologyfn):
  f = open('../data/topologies/' + group + '/' + topologyfn, 'r')
  lines = [line.strip() for line in f.readlines()]
  if topologyfn.endswith('.ntfl'):
    s = set()
    for line in lines:
      line = line.split(' ')
      s.add(line[0])
      s.add(line[1])
    return len(s), len(lines)
  elif topologyfn.endswith('.graph'):
    V = -1
    E = -1
    for line in lines:
      line = line.split(' ')
      if line[0] == 'NODES':
        V = int(line[1])
      if line[0] == 'EDGES':
        E = int(line[1])
    return V, E
  elif topologyfn.endswith('.json'):
    data = read_json('../data/topologies/' + group + '/' + topologyfn)
    return data['V'], data['E']
  assert False

def dict_to_bar(d):
  kmax = -1
  for k in d:
    kmax = max(kmax, k)
  x = [ 0 ] * (kmax + 1)
  for k in d:
    x[k] = d[k]
  return x

def array_to_percent(a):
  s = sum(a)
  return [ 100 * x / s for x in a ]

def data_to_bar(a):
  a = count(a)
  a = dict_to_bar(a)
  a = array_to_percent(a)
  return a

def make_g_barplot(values, group_labels, names, colors, xlbl, ylbl, title, outputfn, width = 0.35):
  import numpy as np
  import matplotlib.pyplot as plt
  n = -1
  for v in values:
    n = max(n, len(v))
  for v in values:
    while len(v) != n:
      v.append(0)
  fig = plt.figure()
  ind = np.array([ (n - 1) * width * i / 4 for i in range(n) ])
  ax = fig.add_subplot(111)
  i = 0
  rects = [ ]
  for v in values:
    rect = ax.bar(ind + i * width, v, width, color=colors[i])
    i += 1
    rects.append(rect)
  # add some
  ax.set_xlabel(xlbl)
  ax.set_ylabel(ylbl)
  ax.set_title(title)
  delta = width * (len(names) - 1) / 2
  ax.set_xticks(ind + delta)
  ax.set_xticklabels(group_labels)
  ax.legend( [r[0] for r in rects], names )
  plt.savefig(outputfn, format='eps', dpi=600, bbox_inches='tight')
  
