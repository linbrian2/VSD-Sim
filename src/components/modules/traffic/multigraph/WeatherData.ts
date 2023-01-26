export interface WeatherData {
  id: number;
  name: string;
  data: Data;
}

export interface Data {
  temp: GraphData;
  relHumidity: GraphData;
  windAvg: GraphData;
  visibility: GraphData;
  precip: GraphData;
}

export interface GraphData {
  data: Datum[];
  xAxis: string;
  yAxis: string;
  title: string;
  subtitle: string;
}

export interface Datum {
  name: string;
  color: string;
  data: Array<number[]>;
}
