package edu.whut.skinhealth.controller;

import edu.whut.skinhealth.entity.User;
import edu.whut.skinhealth.po.DiseaseCount;
import edu.whut.skinhealth.entity.Record;
import edu.whut.skinhealth.po.RecordRequest;
import edu.whut.skinhealth.po.UserInfo;
import edu.whut.skinhealth.service.RecordService;
import edu.whut.skinhealth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("record")
public class RecordController {

    @Autowired
    private RecordService recordService;

    @Autowired
    private UserService userService;

    @GetMapping("queryCount")
    private ResponseEntity<List<DiseaseCount>> queryCount(){
        try {
            return new ResponseEntity<>(recordService.getAllDiseaseCounts(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("queryByMonth")
    private ResponseEntity<Map<String, Map<String, Long>>> queryCountByMonth(){
        try {
            return new ResponseEntity<>(recordService.getAllDiseaseCountsByMonth(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping
    private ResponseEntity<Record> addRecord(@RequestBody RecordRequest recordRequest){
        try{
            User user = userService.findUserByUsername(recordRequest.getUsername());
            if (user == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            UserInfo userInfo = new UserInfo();
            userInfo.setId(user.getId());
            userInfo.setUsername(user.getUsername());
            userInfo.setBirthday(user.getBirthday());
            userInfo.setGender(user.getGender());
            userInfo.setDistrict(user.getDistrict());

            Record record = new Record();
            record.setUser(userInfo);
            record.setDisease(recordRequest.getDisease());
            record.setProbability(recordRequest.getProbability());
            record.setTopResults(recordRequest.getTopResults());
            record.setImageData(recordRequest.getImageData());
            record.setAdviceBrief(recordRequest.getAdviceBrief());
            record.setAdviceTreatment(recordRequest.getAdviceTreatment());
            record.setTime(new Timestamp(new Date(System.currentTimeMillis()).getTime()));

            return new ResponseEntity<>(recordService.addRecord(record), HttpStatus.OK);
        }catch(Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("query")
    private ResponseEntity<List<Record>> queryAllRecords(){
        try{
            return new ResponseEntity<>(recordService.getAllRecords(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("user/{username}")
    private ResponseEntity<List<Record>> queryRecordsByUsername(@PathVariable("username") String username){
        try{
            return new ResponseEntity<>(recordService.getRecordsByUsername(username), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("recent")
    private ResponseEntity<List<Record>> queryRecentRecords(){
        try{
            return new ResponseEntity<>(recordService.getRecentRecords(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("search")
    private ResponseEntity<List<Record>> searchRecords(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String disease,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate){
        try{
            return new ResponseEntity<>(recordService.searchRecords(
                    username,
                    disease,
                    toStartTimestamp(startDate),
                    toEndTimestamp(endDate)
            ), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("export")
    private ResponseEntity<byte[]> exportRecords(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String disease,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate){
        try{
            List<Record> records = recordService.searchRecords(
                    username,
                    disease,
                    toStartTimestamp(startDate),
                    toEndTimestamp(endDate)
            );
            String csv = buildCsv(records);
            byte[] bytes = ("\uFEFF" + csv).getBytes(StandardCharsets.UTF_8);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("text", "csv", StandardCharsets.UTF_8));
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=diagnosis-records.csv");
            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("summary")
    private ResponseEntity<Map<String, Long>> querySummary(){
        try{
            Map<String, Long> summary = new HashMap<>();
            summary.put("userTotal", (long) userService.findAllUser().size());
            summary.put("recordTotal", (long) recordService.getAllRecords().size());
            summary.put("todayRecords", recordService.countTodayRecords());
            summary.put("last7DaysRecords", recordService.countLast7DaysRecords());
            return new ResponseEntity<>(summary, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("dimensions")
    private ResponseEntity<Map<String, Map<String, Long>>> queryDimensions(){
        try{
            Map<String, Map<String, Long>> dimensions = new HashMap<>();
            dimensions.put("gender", recordService.getRecordCountsByGender());
            dimensions.put("district", recordService.getRecordCountsByDistrict());
            return new ResponseEntity<>(dimensions, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Timestamp toStartTimestamp(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return Timestamp.valueOf(LocalDate.parse(value).atStartOfDay());
    }

    private Timestamp toEndTimestamp(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return Timestamp.valueOf(LocalDate.parse(value).atTime(23, 59, 59));
    }

    private String buildCsv(List<Record> records) {
        StringBuilder builder = new StringBuilder();
        builder.append("ID,Username,Disease,Probability,Time,Gender,District,Birthday\n");
        for (Record record : records) {
            UserInfo user = record.getUser();
            builder.append(csv(record.getId()))
                    .append(",").append(csv(user == null ? "" : user.getUsername()))
                    .append(",").append(csv(record.getDisease()))
                    .append(",").append(csv(record.getProbability()))
                    .append(",").append(csv(record.getTime()))
                    .append(",").append(csv(user == null ? "" : user.getGender()))
                    .append(",").append(csv(user == null ? "" : user.getDistrict()))
                    .append(",").append(csv(user == null ? "" : user.getBirthday()))
                    .append("\n");
        }
        return builder.toString();
    }

    private String csv(Object value) {
        String text = value == null ? "" : String.valueOf(value);
        return "\"" + text.replace("\"", "\"\"") + "\"";
    }


}
