const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp(functions.config().firebase);

exports.everyDayTask = functions.pubsub.schedule("0 0 * * *")
    .onRun(async () => {
      console.log("everyDayTask - starting ...");
      const updates = {};
      const db = admin.database();
      const snapshot = await db.ref("GameCount").once("value");
      snapshot.forEach((childSnapshot) => {
        console.log("child snap shot key is ", childSnapshot.key);
        updates["/GameCount/" + childSnapshot.key + "/count"] = 0;
      });
      return db.ref().update(updates)
          .then(() => {
            console.log("schedule 작동 완료!");
          }).catch((error) => {
            console.log("index js error is : ", error);
          });
    });
