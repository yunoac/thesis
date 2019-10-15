import utils

if __name__ == '__main__':
  labels = [ ]
  max_seg = { }
  for group in utils.listGroups():
    for fn in utils.listGroupInstances(group):
      name = fn.split('.')[0]
      data = utils.read_data(group, 'MCFsegcost', name)
      if data == None: continue
      nb_seg = data['nbSeg']
      m = max(nb_seg)
      if m not in max_seg:
        max_seg[m] = 0
      max_seg[m] += 1
  mseg = utils.array_to_percent(utils.dict_to_bar(max_seg))
  groups = [ str(i) for i in range(len(mseg)) ]
  utils.make_g_barplot([mseg], groups, ['seg cost'], ['#3CAEA3'], 'maximum segment cost', 'percentage of topologies', 'maximum segment cost over all topologies <= 50', '../data/plot/mcf_seg.eps', 10)
