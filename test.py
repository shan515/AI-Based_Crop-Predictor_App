import pickle

def print_water():
    print(max_area)


if __name__ == '__main__':
    f = open('/home/sravanchittupalli/konnoha/clones/Lets_HackIT/dataset/ground_water_dic.pkl','rb')
    ground_water = pickle.load(f)
    f.close()

    f = open('dataset/max_area_groundwater.pkl','rb')
    max_area = pickle.load(f)
    f.close()

    print_water()

    