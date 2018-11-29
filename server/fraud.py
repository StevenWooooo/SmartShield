import socket
import struct
import pcapy
import time
from manuf import manuf

devs=pcapy.findalldevs()
print(devs)
inf=devs[5]
print(inf)
cap= pcapy.open_live(inf, 65536 , 1, 0)

def main():
	lst=[]
	i=0
	while True:
		#print(len(cap))
		header, payload= cap.next()
		dest_mac, src_mac, eth_proto, data= ethrnet_frame(payload)
		#print('\nEthernet Frame:')
		a=('Desitantion: {}, Source:{}, Protocol:{}'.format(dest_mac,src_mac,eth_proto))
		p1 = manuf.MacParser(update=True)
		if src_mac== 'F6:5C:89:8C:45:64':
			src_val= 'Local'
		else:
			src_val=p1.get_manuf(src_mac)
		p2 = manuf.MacParser(update=True)
		if dest_mac== 'F6:5C:89:8C:45:64':
			dst_val= 'Local'
		else:
			dst_val=p2.get_manuf(dest_mac)
		#print(src_val)
		#print(dst_val)
		i=i+1
		if (i==7):
			print('ALERT!!!!')
		#if src_val==None or dst_val== None:
		#	print(a)
		else:
			print('.')
		lst.append(a)
		
def ethrnet_frame(data):
	dest_mac, src_mac, proto= struct.unpack('!6s6sH', data[:14])
	return get_mac_addr(dest_mac), get_mac_addr(src_mac), socket.htons(proto), data[14:]

def get_mac_addr(bytes_addr):
	bytes_str= map('{:02x}'.format, bytes_addr)
	return ":".join(bytes_str).upper()
main()