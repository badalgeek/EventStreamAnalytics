export class TotalCustomer {
    deviceId:string;
    total:number;

    getDeviceId() {
        return this.deviceId;
    }

    setDeviceId(deviceId:string) {
        this.deviceId = deviceId;
    }

    getTotal() {
        return this.total;
    }

    setTotal(total:number) {
        this.total = total;
    }
}
