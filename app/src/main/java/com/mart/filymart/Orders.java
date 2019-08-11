package com.mart.filymart;

public class Orders{
        private String id;
        private String name;
        private int numOfSongs;
        private String quantity;
        private String thumbnail;

        public Orders() {
        }

        public Orders(String id, String name, int numOfSongs, String quantity, String thumbnail) {
            this.id = id;
            this.name = name;
            this.numOfSongs = numOfSongs;
            this.quantity = quantity;
            this.thumbnail = thumbnail;
        }

        public String getId(){
            return id;
        }

        public void setId(String id){
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

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }
}

