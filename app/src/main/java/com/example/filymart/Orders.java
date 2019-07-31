package com.example.filymart;

public class Orders{
        private int id;
        private String name;
        private int numOfSongs;
        private String thumbnail;

        public Orders() {
        }

        public Orders(int id, String name, int numOfSongs, String thumbnail) {
            this.id = id;
            this.name = name;
            this.numOfSongs = numOfSongs;
            this.thumbnail = thumbnail;
        }

        public int getId(){
            return id;
        }

        public void setId(int id){
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getNumOfSongs() {
            return numOfSongs;
        }

        public void setNumOfSongs(int numOfSongs) {
            this.numOfSongs = numOfSongs;
        }

        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }
}

