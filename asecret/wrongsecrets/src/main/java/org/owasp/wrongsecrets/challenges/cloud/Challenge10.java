package org.owasp.wrongsecrets.challenges.cloud;


import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.owasp.wrongsecrets.RuntimeEnvironment.Environment.AWS;
import static org.owasp.wrongsecrets.RuntimeEnvironment.Environment.GCP;
import static org.owasp.wrongsecrets.RuntimeEnvironment.Environment.AZURE;

@Component
@Slf4j
@Order(10)
public class Challenge10 extends CloudChallenge {

    private final String awsDefaultValue;
    private final String challengeAnswer;

    public Challenge10(ScoreCard scoreCard,
                       @Value("${secretmountpath}") String filePath,
                       @Value("${default_aws_value}") String awsDefaultValue,
                       RuntimeEnvironment runtimeEnvironment) {
        super(scoreCard, runtimeEnvironment);
        this.awsDefaultValue = awsDefaultValue;
        this.challengeAnswer = getCloudChallenge9and10Value(filePath, "wrongsecret-2");
    }

    @Override
    public Spoiler spoiler() {
        return new Spoiler(challengeAnswer);
    }

    @Override
    public boolean answerCorrect(String answer) {
        return challengeAnswer.equals(answer);
    }

    private String getCloudChallenge9and10Value(String filePath, String fileName) {
        try {
            return Files.readString(Paths.get(filePath, fileName));
        } catch (Exception e) {
            log.warn("Exception during reading file ({}/{}}), defaulting to default without AWS", filePath, fileName);
            return awsDefaultValue;
        }
    }

    public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
        return List.of(GCP, AWS, AZURE);
    }
}
