export interface TrafficFlow {
    id:   number;
    name: string;
    data: Data;
}

export interface Data {
    boundData:  BLMData;
    laneData:   BLMData;
    minuteData: BLMData;
}

export interface BLMData {
    speed:     GraphData[];
    volume:    GraphData[];
    occupancy: GraphData[];
}

export interface GraphData {
    name:  string;
    bound: Bound;
    color: string;
    data:  Array<number[]>;
}

export enum Bound {
    Nb = "NB",
    Sb = "SB",
}
