import sys
import logging
import modbus_tk
import modbus_tk.defines as cst
import struct


import modbus_tk.modbus_tcp as mt
import modbus_tk.defines as md


def main():
    logger = modbus_tk.utils.create_logger(name="console", record_format="%(message)s")
    logger.info("this is a logger")
    master = mt.TcpMaster("127.0.0.1", 502)
    master.set_timeout(5.0)
    aa = master.execute(slave=1, function_code=md.READ_HOLDING_REGISTERS, starting_address=0, quantity_of_x=3)
    print(aa)



if __name__ == '__main__':
    main()
