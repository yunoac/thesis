import utils

if __name__ == '__main__':
  labels = [ ]
  kmin_cnt = { }
  kmax_cnt = { }
  for group in utils.listGroups():
    for fn in utils.listGroupInstances(group):
      name = fn.split('.')[0]
      data = utils.read_data(group, 'KminKmax', name)
      print(data)
      kmin = data['kmin']
      if kmin not in kmin_cnt:
        kmin_cnt[kmin] = 0
      kmin_cnt[kmin] += 1
      kmax = data['kmax']
      if kmax not in kmax_cnt:
        kmax_cnt[kmax] = 0
      kmax_cnt[kmax] += 1
  kmin = utils.array_to_percent(utils.dict_to_bar(kmin_cnt))
  kmax = utils.array_to_percent(utils.dict_to_bar(kmax_cnt))
  print(kmin)
  print(kmax)
  groups = [ str(i) for i in range(len(kmax)) ]
  utils.make_g_barplot([kmin, kmax], groups, ['kmin', 'kmax'], ['#3CAEA3', '#ED553B'], 'value of kmin and kmax', 'percentage of topologies', 'kmin and kmax over all topologies', '../data/plot/kminkmax.eps', 5)
